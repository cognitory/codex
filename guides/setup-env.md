Before setting up your environment, you may want to read [[guides/working-in-the-shell]].

## Download a text editor

If you already have a preferred [[tldrs/text-editor|text editor]], use that; otherwise, download [[tldrs/atom-editor|Atom]] from `https://atom.io`

## Install system programs

Inside a [[tldrs/terminal]] window, do the following:

Install [[tldrs/homebrew]]:

```sh
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```

Install [[tldrs/oh-my-zsh]]:

```sh
sh -c "$(curl -fsSL https://raw.github.com/robbyrussell/oh-my-zsh/master/tools/install.sh)"
```

Install rlwrap:

```sh
brew install rlwrap
```

Install [[tldrs/lein]]:

```sh
brew install leiningen
```

While installing [[tldrs/lein]] you may be asked to first download and install [[tldrs/java]] on your system.


Install java 1.8:

http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html


