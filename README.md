# snipptor
Snipptor is an application for detecting vulnerable code snippets on the internet.
We want to achieve awareness among the developers about the security level of random code snippets.

# How it works
When a snippet is submmited, Snipptor will search for matching vulnerabilities by engine scan and pre defined rules. This way the user who scanned the snippet will be informed in real time which vulnerabilities exist in the snippet and decide if and how to use it safely.

Engine should implement specific api to be able to scan for Snipptor, currently the main engine is YARA scanner
(https://c99.sh/hunting-0days-with-yara-rules Recommnded article to understand how YARA helps to classify snippets)

# How to use
1. Install the chrome extension: https://chrome.google.com/webstore/detail/snipptor/dfljpilhdmgblfkmlhobfmbngehdpjdl
2. When you enter StackOverflow, the code snippets will be marked as one of: "Safe", "Vulnerable" + vulnerabilities, "Malicious" or "No vulnerabilities detected"
3. Choose how to act considering the snippet classification
4. PROFIT

<img width="416" alt="screen - security misconfiguration" src="https://user-images.githubusercontent.com/18406094/175779876-78e6887f-1335-4b6f-957a-6e519e61d052.png">

# Privacy
Note that Snipptor only saves the snippet itself and the origin url, further information will not be saved!

This project is using JHipster (Java, JS, React, Postgre)

