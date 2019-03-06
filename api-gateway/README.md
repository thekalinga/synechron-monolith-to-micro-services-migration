```sh
seq 30 | parallel --gnu -n 0 "http -v GET :8080/order-service/orders \"Cookie: SESSION=c8616dc8-16ff-4a99-b576-6bdc696573e1\""
```
