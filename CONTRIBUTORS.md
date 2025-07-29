# Contributing to Keycloak MCP Server
Thank you for your interest in contributing to Keycloak MCP Server! This document provides guidelines and instructions for contributors.

## Code of Conduct
Please be respectful and considerate of others when contributing to this project. We aim to foster an inclusive and welcoming community.

## How to Contribute

### Reporting Issues
If you find a bug or have a suggestion for improving Keycloak MCP Server:
1. Check if the issue already exists in the GitHub Issues
2. If not, create a new issue with a descriptive title and detailed information about:
   - What you expected to happen
   - What actually happened
   - Steps to reproduce the issue
   - Your environment (OS, Java version, Keycloak version, etc.)

### Pull Requests
Pull requests are welcomed for bug fixes, improvements, and new features:
1. Fork the repository
2. Create a new branch for your changes
3. Make your changes
4. Write or update tests as needed
5. Ensure all tests pass with `./gradlew test`
6. Make a sanity test locally, to check nothing is broken
7. Submit a pull request

### Pull Request Process
1. Update the README.md or documentation with details of changes if appropriate
2. The PR should work on all supported platforms (Linux, macOS, Windows)
3. PRs require review from at least one maintainer before merging
4. Once approved, a maintainer will merge your PR

## Development Workflow
1. Clone your fork of the repository
2. Set up the development environment as described in the DEVELOPERS.md file
3. Make your changes in a new git branch
4. Test your changes thoroughly
5. Push your branch to GitHub and submit a pull request

## Coding Standards
- Follow the existing code style in the project
- Write clear, readable, and maintainable code
- Include comments where necessary
- Write unit tests for new functionality
- Follow Java best practices

## Working with Keycloak
When contributing features that interact with Keycloak:
1. Ensure your code works with the supported Keycloak versions
2. Test your changes against a running Keycloak instance
3. Document any Keycloak-specific configuration requirements

## License
This project is distributed under the MIT License. See the `LICENSE` file for more information.