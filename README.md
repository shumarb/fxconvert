# FXConvert
## Overview
FXConvert is a currency conversion system that supports conversions for 148 currencies and accommodates multiple users.

## Prerequisites
- Java 17.
- Git.
- IntelliJ IDEA.

## Setup
1. `Clone` this repository.
2. Open the `IntelliJ IDE`.
3. Select `Open` and navigate to the cloned repository's location.

## Instructions
1. Review the contents of `users.json` before performing any currency conversions. (File location: `src/main/resources/users.json`).
2. Right-click on the `Runner class` file (File location: `src/main/java/main/Runner.java`).
3. Select `Run 'Runner.main()'`.
4. Check the `console` for messages indicating the results of the currency conversion attempts.
5. Examine the `logging.log` file created after running the application. Its contents will correspond to the `console` messages.
6. To `reset users.json` to its original state, run `git restore src/main/resources/users.json` in the terminal.

## Tech Stack
- Programming: Java.
- Frameworks/Tools: JUnit, Git.
