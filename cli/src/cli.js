import vorpal from 'vorpal'
import { words } from 'lodash'
import { connect } from 'net'
import { Message } from './Message'

export const cli = vorpal()

let username
let server

let alreadyRenamed = false

let portNumber = 8080//default socket value
let hostName = 'localhost'//default host name
const commandWords = ['connect', 'disconnect', 'echo', 'users', 'broadcast', 'exit', 'server'];//convenient array of command words
let previousCommand = ''//default previous command

cli
  .delimiter(cli.chalk['yellow']('ftd~$'))

cli
  .mode('connect <username> <port> <host>')
  .delimiter(cli.chalk['green']('connected>'))
  .init(function (args, callback) {
    username = args.username
    username = username.toLowerCase()

    if(!/^[a-z]*$/.test(username) || commandWords.indexOf(username) !== -1)
    {//username should be a-z, no caps because caps confuse Vorpal, as do special characters
      //username should not be a command, because that would confuse everyone
      username = "user"
      this.log("Invalid username. I will think of a new name for you.");
    }  

    //get port number and host name
    portNumber = parseInt(args.port)
    hostName = args.host

    server = connect({ host: hostName, port: portNumber }, () => {
      server.write(new Message({ username, command: 'connect' }).toJSON() + '\n')
      callback()
    })

    server.on('data', (buffer) => {
      //handle server input
      //display the message and give it a different color for each command
      const mess = Message.fromJSON(buffer);
      if(mess.username === 'SERVER' && !alreadyRenamed)
      {//I'm probably being renamed
        if(mess.contents.indexOf("Welcome") === -1)
        {//the name is not accepted
          const arr = words(mess.contents);
          username = arr[9];//this word will be the new name
        }  
      }  
      alreadyRenamed = true;
      
      if(mess.command === 'broadcast')
        this.log(cli.chalk['cyan'](mess.contents));
      else if(mess.command === 'users')
        this.log(cli.chalk['magenta'](mess.contents));
      else if(mess.command === 'echo')
        this.log(cli.chalk['grey'](mess.contents));
      else if(mess.command === 'connect')
        this.log(cli.chalk['green'](mess.contents));
      else if(mess.command === 'disconnect')
        this.log(cli.chalk['red'](mess.contents));
      else//direct message
        this.log(cli.chalk['white'](mess.contents));
    })

    server.on('end', () => {
      cli.exec('exit')
    })
  })
  .action(function (input, callback) {
    //handle user input
    const inputWords =  words(input)
    if(inputWords.length !== 0 && previousCommand !== '' && (inputWords[0] !== 'disconnect' && inputWords[0] !== 'echo' && inputWords[0] !== 'users' && inputWords[0] !== 'broadcast' && !input.startsWith("@") ))
    {//we have a previous command and the user is not trying to do another command
      //the user typed in at least one word
      //we have a previous command
      //the user did not enter echo, did not enter users, did not enter @, did not enter broadcast, did not enter disconnect
        //thus the user did not enter a command
      //this means the user wants to use the previous command
      input = previousCommand + " " + input;
    } 
    const containsAt = input.startsWith("@");
    const [ command, ...rest ] = words(input)
    const contents = rest.join(' ')
    if (command === 'disconnect') {
      previousCommand = ''
      server.end(new Message({ username, command }).toJSON() + '\n')
    } else if (command === 'echo') {
      previousCommand = command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command === 'broadcast') {
      previousCommand = command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    }else if (command === 'users') {
      previousCommand = command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    }else if (containsAt) {
      previousCommand = "@" + command
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    }else {
        previousCommand = ''
        this.log(cli.chalk['yellow'](`Command <${command}> was not recognized.\nA command is required!`))
    }

    callback()
  })
