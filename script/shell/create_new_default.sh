#create new default cert
#create Root CA
##CA password:aerohive; keysize=1024; days:7300
#$1:CA configure file $2:Server Cert configure file
password=aerohive
keysize=1024
days=7300

##create CA
openssl genrsa -des3 -out ./new_Default_key.pem -passout pass:$password $keysize
openssl req -new -key ./new_Default_key.pem -out ./hm.csr -passin pass:$password -config ./$1
openssl x509 -req -days $days -in ./hm.csr -signkey ./new_Default_key.pem -out ./new_Default_CA.pem -keyform PEM -outform PEM -passin pass:$password 
  
##create server.csr and key
openssl genrsa -out ./new_Default-Server_key.pem $keysize
openssl req -new -out ./new_Default-Server.csr -key  ./new_Default-Server_key.pem -config ./$2

