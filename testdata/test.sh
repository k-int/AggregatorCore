echo Uploading $1

curl -k "http://localhost:8080/repository/upload" --user "admin:admiN123!" --trace-ascii text.txt -F "owner=testprovider" -F "on_behalf_of=testuser" -F "create_user=Y" -F "user_full_name=Test User Name" -F "authoritative=true" -F "Connection=keep-alive" -F "upload=@$1;type=text/xml"
