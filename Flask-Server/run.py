from app.server_start import server

if __name__ == '__main__':
    server.run(host='0.0.0.0', port=8080, threaded=True)
