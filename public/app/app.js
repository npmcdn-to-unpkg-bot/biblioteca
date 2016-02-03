angular
    .module
        ('architectplay',
            ['ngRoute',
             'ngResource',
             'ngMaterial'
            ]
        )
    .config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: '/assets/app/views/home.html',
                controller: 'home.controller'
            })
            .when('/contato', {
                templateUrl: '/assets/app/views/contato.html'
            })
            .when('/usuarios/novo', {
                templateUrl: '/assets/app/views/usuarios/create.html',
                controller: 'usuario.create.controller'
            })
            .when('/usuarios/detalhe/:id', {
                templateUrl: '/assets/app/views/usuarios/detail.html',
                controller: 'usuario.detail.controller'
            })
            .when('/usuarios/editar/:id', {
                templateUrl: '/assets/app/views/usuarios/edit.html',
                controller: 'usuario.edit.controller'
            })
            .when('/usuarios', {
                templateUrl: '/assets/app/views/usuarios/list.html',
                controller: 'usuario.list.controller'
            })
            .when('/livros', {
                templateUrl: '/assets/app/views/livros/list.html',
                controller: 'livro.controller'
            });
   }).config(function($mdThemingProvider) {
       $mdThemingProvider.definePalette('amazingPaletteName', {
         '50': 'ECEFF1',
         '100': 'C5CAE9',
         '200': 'ef9a9a',
         '300': 'e57373',
         '400': 'ef5350',
         '500': '3F51B5',
         '600': 'e53935',
         '700': 'd32f2f',
         '800': 'c62828',
         '900': 'b71c1c',
         'A100': 'ff8a80',
         'A200': 'ff5252',
         'A400': 'ff1744',
         'A700': 'd50000',
         'contrastDefaultColor': 'light',    // whether, by default, text (contrast)
                                             // on this palette should be dark or light
         'contrastDarkColors': ['50', '100', //hues which contrast should be 'dark' by default
          '200', '300', '400', 'A100'],
         'contrastLightColors': undefined    // could also specify this if default was 'dark'
       });
       $mdThemingProvider.theme('default')
         .primaryPalette('amazingPaletteName')
     });