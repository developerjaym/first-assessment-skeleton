import vorpal from 'vorpal'
import { words } from 'lodash'
import { connect } from 'net'
import { Message } from './Message'

export const cli = vorpal()

let username
let server

let portNumber = 8080//Jay line
let hostName = 'localhost'//Jay line

cli
  .delimiter(cli.chalk['yellow']('ftd~$'))

cli
  //.mode('connect <username>')
  .mode('connect <username> <port> <host>')//Jay line
  .delimiter(cli.chalk['green']('connected>'))
  .init(function (args, callback) {
    username = args.username

    //Jay code below
    portNumber = parseInt(args.port)
    hostName = args.host
    //Jay code above

    //server = connect({ host: 'localhost', port: 8080 }, () => {
    server = connect({ host: hostName, port: portNumber }, () => {
      server.write(new Message({ username, command: 'connect' }).toJSON() + '\n')
      callback()
    })

    server.on('data', (buffer) => {
      const mess = Message.fromJSON(buffer);
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
    const containsAt = input.startsWith("@");
    const [ command, ...rest ] = words(input)
    const contents = rest.join(' ')

    if (command === 'disconnect') {
      server.end(new Message({ username, command }).toJSON() + '\n')
    } else if (command === 'echo') {
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    } else if (command === 'broadcast') {
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    }else if (command === 'users') {
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    }else if (containsAt) {
      server.write(new Message({ username, command, contents }).toJSON() + '\n')
    }else {
      this.log(cli.chalk['yellow'](`Command <${command}> was not recognized`))
    }

    callback()
  })
