docker compose down -v
docker compose up -d
sleep 15

curl -s -k -X POST https://localhost:8443/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@uniba.it",
    "password": "password",
    "fullName": "Super Admin",
    "role": "HR_ADMIN"
}'

curl -s -k -X POST https://localhost:8443/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "mario.rossi@uniba.it",
    "password": "password",
    "fullName": "Mario Rossi",
    "role": "USER"
}'

docker exec -it hr_ollama ollama pull llama3