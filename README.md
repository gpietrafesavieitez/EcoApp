<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/gpietrafesavieitez/EcoApp">
    <img src="images/logo/logo.png" alt="Logo" width="425" height="125">
  </a>
  <br />
  <p align="center">
    Because recycling can also be enjoyable.
    <br />
    <br />
    <br />
    <a href="https://github.com/gpietrafesavieitez/EcoApp">View Demo</a>
    ·
    <a href="https://github.com/gpietrafesavieitez/EcoApp/issues">Report Bug</a>
    ·
    <a href="https://github.com/gpietrafesavieitez/EcoApp/issues">Request Feature</a>
  </p>
</p>

<!-- TABLE OF CONTENTS -->
## Table of Contents
* [About the project](#about-the-project)
  * [Built with](#built-with)
* [Getting Started](#getting-started)
  * [Requirements](#requirements)
  * [Installation](#installation)
* [Usage](#usage)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [Acknowledgements](#acknowledgements)
* [Authors](#authors)
* [License](#license)

<!-- ABOUT THE PROJECT -->
## About the project
Seen general situation of the society around the 3Rs practice through the time, we have realised that most people don't know how to recycle properly. This app was born in order to solve most of the doubts that even we have had when recycling, reusing o repairing products.

First release of this project is an Android app focused only on the recycling part and allows the user to scan any object with a barcode or QR in it and information of how to recycle it will be shown to the user. If the object doesn't exist user will be able to upload it just by clicking a button. Additional features are multilanguage support (English and Spanish at this moment) and the possibility to see another products uploaded by other users.

Main goal would be a social network that will encourage users to have a good 3Rs rule practice in order to reduce contamination and thus global warming. They will be able to interact with the products by sharing suggestions on how to repair/reuse them. Scanning system would be improved by deep learning recognition to not depend only on barcodes to identify a specific object.

We are aware that not everywhere is recycled the same so the final idea is also to internationalize the application and support more languages.

### Built with
* [EcoAppApi](https://github.com/palonsovazquez/EcoAppApi) - EcoApp API built in Python with Django REST Framework.
* [Android Studio](https://developer.android.com/studio) - Main development platform using Kotlin as the programming language.
* [Zxing](https://github.com/zxing/zxing) - Core library for scanning barcode/QR.
* [Retrofit & OkHttp](https://github.com/square/retrofit) - HTTP client core libraries.
* [Firebase](https://firebase.google.com) - OAuth 2.0 Authentication API.
* [Glide](https://github.com/bumptech/glide) - Media managment and image loading/caching library.

<!-- GETTING STARTED -->
## Getting Started
### Requirements
* Minimum - Android 6.0 Marshmallow (API >= 23)
* Recommended - Android 8.0 Oreo (API >= 26)

### Installation
* Download last release and install it on your smartphone. You will need a Google Account.
<p><a href="https://github.com/gpietrafesavieitez/EcoApp/releases/download/v0.1-alpha/EcoApp.apk">EcoApp v0.1-alpha</a></p>
<p>:warning:This is a pre-release version for debugging and may be unstable so when using it maybe you will encounter some bugs, in that case please let us know. Thank you.</p>


<!-- USAGE EXAMPLES -->
## Usage
* Logging into the application

<p align="center">
  <img height="480" width="270" src="images/screenshots/screenshot_1.jpg">
</p>

* Once inside you can view products uploaded by other users, view details of each product to know how to recycle them, look for components, scan a product or upload one if it doesn't exist.

<p align="center">
  <img height="480" width="270" src="images/screenshots/screenshot_2.jpg">
  <img height="480" width="270" src="images/screenshots/screenshot_3.jpg">
  <img height="480" width="270" src="images/screenshots/screenshot_4.jpg">
  <img height="480" width="270" src="images/screenshots/screenshot_5.jpg">
  <img height="480" width="270" src="images/screenshots/screenshot_6.jpg">
</p>

<!-- ROADMAP -->
## Roadmap
See the [open issues](https://github.com/gpietrafesavieitez/EcoApp/issues) for a list of proposed features (and known issues).

<!-- CONTRIBUTING -->
## Contributing 
Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated** :octocat:

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements
* [CPR Daniel Castelao](https://www.danielcastelao.org)
* [OpenFoodFacts](https://es.openfoodfacts.org)
* [Codescanner](https://github.com/yuriy-budiyev/code-scanner)
* [Best README Template](https://github.com/othneildrew/Best-README-Template)

## Authors
* [**Gabriel Pietrafesa**](https://github.com/gpietrafesavieitez)
* [**Pablo Alonso**](https://github.com/palonsovazquez)

<!-- LICENSE -->
## License
:bookmark:
Distributed under the Apache License 2.0. See `LICENSE` for more information.

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/gpietrafesavieitez/EcoApp.svg?style=flat-square
[contributors-url]: https://github.com/gpietrafesavieitez/EcoApp/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/gpietrafesavieitez/EcoApp.svg?style=flat-square
[forks-url]: https://github.com/gpietrafesavieitez/EcoApp/network/members
[stars-shield]: https://img.shields.io/github/stars/gpietrafesavieitez/Ecoapp.svg?style=flat-square
[stars-url]: https://github.com/gpietrafesavieitez/EcoApp/stargazers
[issues-shield]: https://img.shields.io/github/issues/gpietrafesavieitez/EcoApp.svg?style=flat-square
[issues-url]: https://github.com/gpietrafesavieitez/EcoApp/issues
[license-shield]: https://img.shields.io/github/license/gpietrafesavieitez/EcoApp.svg?style=flat-square
[license-url]: https://github.com/gpietrafesavieitez/EcoApp/blob/master/LICENSE
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/gabriel-pietrafesa-vi%25C3%25A9itez-126248122
[product-screenshot]: images/screenshot.png
