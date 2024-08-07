mongosh -u root -p 1234 <<EOF
use echo
db.createUser({user: "root", pwd: "1234", roles: [{ role: "readWrite", db: "echo" }]})
db.text.insert({contents:"test"})
EOF