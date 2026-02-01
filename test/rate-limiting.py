import requests
import urllib3
import time
import sys
import concurrent.futures

# --- CONFIGURAZIONE ---
BASE_URL = "https://localhost:8443"
EMAIL = "admin@hr.com"
PASSWORD = "admin123"
NUM_REQUESTS = 20

# Disabilita avvisi SSL
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

def get_auth_token():
    print("Login", end=" ", flush=True)
    try:
        response = requests.post(
            f"{BASE_URL}/auth/login", 
            json={"email": EMAIL, "password": PASSWORD}, 
            verify=False, 
            timeout=10
        )
        if response.status_code == 200:
            data = response.json()
            token = data.get("accessToken") or data.get("token")
            print("Token ottenuto")
            return token
        else:
            print(f"Login Fallito ({response.status_code})")
            sys.exit(1)
    except Exception as e:
        print(f"Errore: {e}")
        sys.exit(1)

def send_single_request(token, i):
    url = f"{BASE_URL}/api/chat"
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    payload = {"question": "Ciao, sto provando uno stress test"}
    
    try:
        start = time.time()
        # Timeout basso: non ci interessa la risposta dell'AI, ci interessa lo status code HTTP
        response = requests.post(url, json=payload, headers=headers, verify=False, timeout=60)
        duration = time.time() - start
        return i, response.status_code, duration
    except Exception as e:
        return i, "ERR", 0.0

def attack_mode_async(token):
    print(f"\nLanciando({NUM_REQUESTS} richieste in parallelo.")

    with concurrent.futures.ThreadPoolExecutor(max_workers=NUM_REQUESTS) as executor:
        futures = [executor.submit(send_single_request, token, i) for i in range(1, NUM_REQUESTS + 1)]
        
        for future in concurrent.futures.as_completed(futures):
            idx, code, duration = future.result()
            
            if code == 200:
                print(f"Request {idx:02d}: 200 OK ({duration:.2f}s) - Passata")
            elif code == 429:
                print(f"Request {idx:02d}: 429 BLOCCATO ({duration:.2f}s) - Superato il rate limit")
            else:
                print(f"Request {idx:02d}: {code} ({duration:.2f}s)")

if __name__ == "__main__":
    token = get_auth_token()
    if token:
        start_global = time.time()
        attack_mode_async(token)
        print(f"\nTempo di esecuzione: {time.time() - start_global:.2f}s")