# Finch FS2 issue

### Reproducer:
```
$ sbt runMain Main
$ truncate -s 30M file
$ curl -v -XPOST -T file http://localhost:8080/stream -H 'Expect:' 
```

### Result:
```
$ curl -v -XPOST -T large http://localhost:8080/stream -H 'Expect:' 
* Uses proxy env variable no_proxy == 'localhost,127.0.0.0/8,::1'
*   Trying ::1:8080...
* Connected to localhost (::1) port 8080 (#0)
> POST /stream HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.71.1
> Accept: */*
> 
* Recv failure: Connection reset by peer
* Closing connection 0
curl: (56) Recv failure: Connection reset by peer
```
