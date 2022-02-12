docker rm -f cawandu
docker pull xannosz/cawandu
docker run -d --name=cawandu --restart=always \
-p 7777:7777 \
-v /var/log/apps:/var/log/apps \
-v /var/run/docker.sock:/var/run/docker.sock \
-v /var/lib/docker/containers:/var/lib/docker/containers \
xannosz/cawandu:latest