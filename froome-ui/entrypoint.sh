#!/bin/sh

envsubst '${user_api_url} ${product_api_url} ${payment_api_url} ${order_api_url}' < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf

nginx -g 'daemon off;'
