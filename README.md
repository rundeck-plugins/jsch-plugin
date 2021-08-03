# jsch-plugin Rundeck Plugin

This is a node executor / File Copier plugin based on [JSCH](http://www.jcraft.com/jsch/) library

This code was extracted from the Rundeck core, and is the standalone version of the same plugin
that is built-in and shipped with Rundeck.

## Build and Install

```
./gradlew clean && ./gradlew build 

cp build/libs/jsch-plugin-x.x.x.jar $RDECK_BASE/libext
```

## How to use

This plugin is a standalone version of the JSCH plugin that comes built into Rundeck.
The JSCH [documentation](https://docs.rundeck.com/docs/administration/projects/node-execution/ssh.html)  applies to this plugin.

### Set it at project level

Go to `Project Settings > Edit Configuration` to set the JSCH plugin at project level

### Set at node level

Use `node-executor` and `file-copier` node attributes to use SSHJ plugin at node level.

```
Demo-Ubuntu:
  nodename: Demo-Ubuntu
  hostname: 192.168.100.18
  description: Ubuntu 20
  username: samuel
  osFamily: unix
  node-executor: jsch-ssh
  file-copier: jsch-scp
  tags: ubuntu
  ssh-authentication: password
  ssh-password-storage-path: keys/node/samuel.password
  sudo-password-storage-path: keys/node/sudo.password
  ssh-password-option: option.password
  sudo-command-enabled: 'true'
  
```

