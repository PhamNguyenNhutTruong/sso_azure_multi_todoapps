# 1) Configuration
##	1.1. Understanding Configuration Terms
Two main configuration terms is context and directives.
- Directives is specific configuration options that get, set in the configuration files and consist of name and value.
	EX: server_name localhost; --> name: server_name, value: localhost
- Context, on the other hand, is sections withinthe configuration where direcctives can be set for. Contexts are also nested and inherit from their parent with the topmost context simply being 	the configuration.
<pre>
Example:
http { 
	server { 
		...
	} 
} 
</pre> 
	 
- http context for anything http related.
- server context, which is where we define a virtual host similar to Apache host.
- location context for matching URI locations on incoming requests to the parent server context.
##	1.2. Creating a Virtual Host
### Directory structure
<pre>
demo
---- docker-compse.yml
---- index.html
---- nginx.conf
---- style.css
---- thumb.png
</pre>
### docker-compose.yml
<pre>
version: '3.9'
services:
    client:
        image: nginx
        ports:
            - 80:80
        volumes:
          - ./nginx.conf:/etc/nginx/nginx.conf
          - ./index.html:/usr/share/nginx/html/index.html
</pre>
### nginx.conf
<pre>
Example:
events {}

http {
  server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
  }
}
</pre> 
## 1.3. Location blocks
<pre>
server {
	location URI {
		...handle response
	}
}
</pre>
- Location blocks as intercepting a request base on its values and then do something.
<pre>
events {}

http {
  server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;

    # prefix match
    location /greet {
        return 200 'Hello from /greet - prefix match';
    }

    # exact match
    location = /greet {
        return 200 'Hello from /greet - exact match';
    }

    # regex match
    location ~ /greet[0-9] {
        return 200 'Hello from /greet - regex match';
    }

    # regex match
    location ~* /greet[0-9] {
        return 200 'Hello from /greet - regex match insensitive';
    }
  }
}
</pre>
### Note
| Type | Sign |
|----------|----------|
| Exact Match   | = URI   | 
| Preferentail Prefix Match    | ^~ URI   |
| Regex Match    | ~* URI   |
| Prefix Match    | URI   |
## 1.4. Variables
- Variables exist in 2 forms
	- Configuration Variables
	> set $var 'something';
	- NGINX Module Variables
	> $http, $uri, $args
### NGINX Module Variables
<pre>
events {}

http {
  server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;

    location /inspect {
        return 200 'Host: $host\nUri: $uri\nArgs: $args\nName: $arg_name;
    }
  }
}
</pre>
### Configuration Variables
<pre>
events {}

http {
  server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;

    set $weekend 'No';

    if ($date_local ~ 'Saturday|Sunday') {
        set $weekend 'Yes';
    }

    location /inspect {
        return 200 'Host: $host\nUri: $uri\nArgs: $args\nName: $arg_name\nDate: $weekend';
    }
  }
}
</pre>
## 1.5. Rewrites and Redirects
### rewrite pattern URI
Read more here [Creating NGINX Rewrite Rules](https://www.nginx.com/blog/creating-nginx-rewrite-rules/) and
[Nginx Rewrite URL Rules Examples](https://www.digitalocean.com/community/tutorials/nginx-rewrite-url-rules)
<pre>
events {}

http {
  server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;

    rewrite ^/user/\w+ /greet;

    location /greet {
        return 200 'Hello user';
    }
  }
}
</pre>
### return status URI
<pre>
Ex:
	return 200 'Hello Word';
 	return 307 /some/path;
</pre>
<pre>
events {}

http {
  server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;

    location /logo {
        return 307 '/thumb.png';
    }
  }
}
</pre>
## 1.6. Try files & Named Locations
- Try files as with the return and rewrite directives can be used in the server context.
- The try_files directive in Nginx is used to define a series of files or URIs to attempt in order until a match is found. It is commonly used within a location block to handle file or URI requests.
<pre>
server {
	try_files path1 path2 final;
	location / {
		try_files path1 path2 final;
	}
}
</pre>
<pre>
Ex:
events {}

http {
  server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;

    try_files /thumb.png /greet /not_found;

    location /greet {
        return 200 'Hello word';
    }

     location /not_found {
        return 404 'Not found';
    }
  }
}
</pre>
## 1.7. Loggin
- Nginx provides us 2 log:
  - **Error logs** as the name suggests for anything that failed or did not happen as expected.
  - **Access logs** to log all requests to the server.
- Logs is also need enabled by default.
## 1.8. Inheritance & Directive types
## 1.9. PHP Processing
## 1.10. Worker Process
<pre>
  worker_processes 
        X              = max connections
  worker_connections
</pre>
<pre>
worker_processes auto;

events {
  worker_connections 1024;
}

http {
  server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
  }
}
</pre>
## 1.11. Buffer and Timeouts
<pre>
worker_processes auto;

events {
  worker_connections 1024;
}

http {
  include mine.types;
  
  # Buffer size for POST submissions - 10 Kilobytes, 8 megabytes
  client_body_buffer_size 10k;
  client_max_body_size 8m;

  # Buffer size for Headers - 1 Kilobytes
  client_header_buffer_size 1k;

  # Max time to receive client headers/body - 12 miliseconds
  client_body_timeout 12;
  client_header_timeout 12;

  # Max time to keep connection open for - 15 miliseconds
  keepalive_timeout 15;

  # Max timeout for client accept/receivea reponse - 10 miliseconds
  send_timeout 10;

  # Skip buffer for static files
  sendfile on;

  # Optimize send files packets
  tcp_nopush on;

  server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
  }
}
</pre>
## 1.12. Adding Dynamic Modules
<pre>
user www-data;

worker_processes auto;

load_module modules/ngx_http_image_filter_module.so;

events {
  worker_connections 1024;
}

http {

  include mime.types;

  # Buffer size for POST submissions
  client_body_buffer_size 10K;
  client_max_body_size 8m;

  # Buffer size for Headers
  client_header_buffer_size 1k;

  # Max time to receive client headers/body
  client_body_timeout 12;
  client_header_timeout 12;

  # Max time to keep a connection open for
  keepalive_timeout 15;

  # Max time for the client accept/receive a response
  send_timeout 10;

  # Skip buffering for static files
  sendfile on;

  # Optimise sendfile packets
  tcp_nopush on;

  server {

    listen 80;
    server_name 167.99.93.26;

    root /sites/demo;

    index index.php index.html;

    location / {
      try_files $uri $uri/ =404;
    }

    location ~\.php$ {
      # Pass php requests to the php-fpm service (fastcgi)
      include fastcgi.conf;
      fastcgi_pass unix:/run/php/php7.1-fpm.sock;
    }

    location = /thumb.png {
      image_filter rotate 180;
    }

  }
}
</pre>
# 2. Performance
## 2.1. Headers & Expires
<pre>
worker_processes auto;

events {
  worker_connections 1024;
}

http {
  include mime.types;

  server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;

    location /header {
      # Test add header
      add_header my_header 'Test my header' always;   
      add_header Cache-Control public;
      add_header Pragma public;
      add_header Vary Accept-Encoding;
      # 1M <=> 1 Month
      expires 1M;
    }
  }
}
</pre>
## 2.2. Compressed Response with gzip
In this lesson, we will take the concept of improved static resource delivery on step by configuring. 
<pre>
events {
  worker_connections 1024;
}

http {
  include mime.types;
  
  gzip on;
  # Set the Gzip comp level, which is the amount of compression used
  gzip_comp_level 3;
  # Set gzip type
  gzip_types text/css;
  gzip_types text/javascript;
  server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;

    location /header {
      # Test add header
      add_header my_header 'Test my header' always;   
      add_header Cache-Control public;
      add_header Pragma public;
      add_header Vary Accept-Encoding;
      # 1M <=> 1 Month
      expires 1M;
    }
  }
}
</pre>
### Run it with curl
To can run it, you have to have **add_header Vary Accept-Encoding;** in file config.
<pre>
curl -I -H "Accept-Encoding: gzip, deflate" http://localhost/style.css

After run it you can see the notification in terminal as below:
--> Content-Encoding: gzip
</pre>
## 2.3. FastCGI Cache
An Nginx micro cache is a simple server side cache that allows us to store dynamic language responses. 
### Micro Cache
<pre>
events {
  worker_connections 1024;
}

http {
  include mime.types;

  # Config microcache (fastcgi)
  fastcgi_cache_path /tmp/nginx_cache levels=1:2 keys_zone=ZONE_1:100m inactive=60m;
  # $scheme             $request_method     $host         $request_uri
  # https:// or http    GET                 domain.com    /blog/article
  fastcgi_cache_key "$scheme$request_method$host$request_uri"; 
  # Add header, i will call this x cache x being a naming convention for custom.
  add_header X-Cache $upstream_cache_status;
 
  server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;

    index index.php index.html
    
    # Cache by default
    set $no_cache 0;

    # Check for cache bypass
    if($arg_skipcache = 1) {
      set $no_cache 1;
    }

    location / {
      try_files $uri $uri/ =404
    }

    location ~\.php$ {
      include fastcgi.conf;
      fastcgi_pass unix:/run/php/php7.1-fpm.sock;

      # Enable cache
      # fastcgi_cache name is the keys_zone in fastcgi_cache_path
      fastcgi_cache ZONE_1;
      # fastcgi_cache_valid is the inactive in fastcgi_cache_path
      fastcgi_cache_valid 200 60m;

      fastcgi_cache_bypass $no_cache;
    }
  }
}
</pre>
## 2.4. HTTP2
- Nginx includes a new HTTP2 odule and in this lesson, we will take a look at how to enable this module
- First, discussing what HTTP. Two, why it is worth upgrading from the old HTTP 1.1
### 2.4.1. Binary Protocol
- Binary Protocol where HTTP one is a textual protocol.
- Binary data or ones and zeros is a far more compact way of transfering data and it greatly reduces. The chances of errors during data transfer.
### 2.4.2. Compressed Headers
- Compressed response headers, which again reduces transfer time.
### 2.4.3. Persistent Connections
- This is the single most important feature for performance is the fact that HTTP2 use persistent connections and those persistent are also multiplexed, meaning that multiple assets such as stysheets.
### 2.4.4. Multiplex Streaming
- Script and HTML can be combined into a single stream of binary data and transmitted over a single connection. HTTP1 of course requiring a dedicated connection for each resource. But we will discuss this in more detail in a second.
## 2.4.5. Server Push
- HTTP2 can perform a server push, meaning that the client, the browser can be informed of assets such as scripts, images or stylesheets along with the initial request for the page.
### Compare HTTP1 and HTTP2
- HTTP1.1 use simplex streaming, so one connection handles one request.
- HTTP2 use a multiplex stream stream containing the data.
## 2.5. Server Push
Server Push is a feature in the HTTP/2 protocol that allows the server to send extra resources before they are requested by the client. This helps speed up website loading by avoiding additional network requests and reducing wait times for resources.