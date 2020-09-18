
# PokerKings
A remake of PokerApp, from scratch. Includes new features, incorporates new concepts and is completely playable.

## Setup client side (Android)
  - Install Android Studio:
    + https://developer.android.com/studio/install
  - Change \utils\SocketManager.java to reflect the IP of your local machine:
```bash
mSocket = IO.socket("http://YOUR_LOCAL_IP:3000/");
```

## Setup server side
Install Node.JS:
  + Ubuntu: sudo apt-get install nodejs
  + Windows: https://nodejs.org/en/#home-downloadhead

Install MongoDB:
  + Ubuntu: https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/
  + Window: https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/
  
Install packages:
```bash
npm install
```
  Start server-side:
```bash
npm start
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)