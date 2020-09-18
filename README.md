
# PokerKings
This is a remake of the [first version](https://github.com/labiguime/PokerApp) of my app where I use Socket.io and Node.JS/Express instead of Firebase. This allows us to separate the game logic from the Android application, which was a major flaw of the first version. The changes were so substantial that I decided to start it again from scratch. This **open-source project** is meant for you if you want to learn Android, improve your github skills, work with Node.JS or React. Everybody can contribute, even if it's only an image. Below is a demonstration of the beta version that was released to the app store and the steps to follow to setup the project.

![Demonstration](/readme-assets/readme-app-demo.gif)
 
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
In the 'Projects' section you will find a list of issues. You can contribute to those if you wish. Pull requests are also welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
[MIT](https://choosealicense.com/licenses/mit/)
