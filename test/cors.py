import requests
import urllib3

TARGET_URL = "https://localhost:8443/api/chat"
ORIGINS = ["http://localhost:5173", "https://google.com"]

urllib3.disable_warnings()

def test_cors(origin):
    headers = {
        "Origin": origin,
        "Access-Control-Request-Method": "POST",
        "Access-Control-Request-Headers": "Content-Type, Authorization",
    }
    
    try:
        r = requests.options(TARGET_URL, headers=headers, verify=False, timeout=5)
        allowed = r.headers.get("Access-Control-Allow-Origin")
        
        print(f"[TEST] Origin: {origin}")
        print(f"  Status: {r.status_code}")
        print(f"  Header: {allowed if allowed else 'None'}")
        
        if allowed == origin:
            print("  Result: MATCH\n")
        else:
            print("  Result: DENIED/MISMATCH\n")
            
    except Exception as e:
        print(f"[ERR] Connection failed for {origin}: {e}\n")

if __name__ == "__main__":
    for o in ORIGINS:
        test_cors(o)