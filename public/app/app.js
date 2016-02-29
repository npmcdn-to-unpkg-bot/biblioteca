angular
    .module
        ('architectplay',
            ['ngRoute',
             'ngResource',
             'ngAria',
             'angular-loading-bar'
            ]
        )
    .config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: '/assets/app/views/home.html',
                controller: 'home.controller',
                activetab: 'home',

            })
            .when('/contato', {
                templateUrl: '/assets/app/views/contato.html',
                controller: 'contato.controller',
                activetab: 'contato'
            })
            .when('/usuarios/novo', {
                templateUrl: '/assets/app/views/usuarios/create.html',
                controller: 'usuario.create.controller',
                activetab: 'usuarios'
            })
            .when('/usuarios/detalhe/:id', {
                templateUrl: '/assets/app/views/usuarios/detail.html',
                controller: 'usuario.detail.controller',
                activetab: 'usuarios'
            })
            .when('/usuarios/editar/:id', {
                templateUrl: '/assets/app/views/usuarios/edit.html',
                controller: 'usuario.edit.controller',
                activetab: 'usuarios'
            })
            .when('/usuarios', {
                templateUrl: '/assets/app/views/usuarios/list.html',
                controller: 'usuario.list.controller',
                activetab: 'usuarios'
            })
            .when('/livros', {
                templateUrl: '/assets/app/views/livros/list.html',
                controller: 'livro.controller',
                 activetab: 'livros'
            })
            .when('/cursos', {
                templateUrl: '/assets/app/views/cursos/list.html',
                controller: 'curso.controller',
                activetab: 'cursos'
            })
            .when('/artigos', {
                templateUrl: '/assets/app/views/artigos/list.html',
                controller: 'artigo.controller',
                activetab: 'artigos'
            })
            .when('/artigos/energiasrenovaveis/biogas', {
                templateUrl: '/assets/app/views/artigos/energiasrenovaveis/biogas.html',
                controller: 'artigo.controller',
                activetab: 'artigos'
            })
            .when('/videos', {
                templateUrl: '/assets/app/views/videos/list.html',
                controller: 'video.controller',
                activetab: 'videos'
            })
            .when('/videos/biogas', {
                templateUrl: '/assets/app/views/videos/biogas.html',
                controller: 'video.controller',
                activetab: 'videos'
            })
            .when('/fotos', {
                templateUrl: '/assets/app/views/fotos/list.html',
                controller: 'foto.controller',
                activetab: 'fotos'
            })
            .when('/direitos', {
                templateUrl: '/assets/app/views/direito.html',
                controller: 'direito.controller',
                activetab: 'direitos'
            });
//            .otherwise({redirectTo:'/'});
   // se tirar esse .run as funções do material design lite
   //não carrega corretamente na página, precisa apertar f5 várias vezes
   }).run(function ($rootScope,$timeout) {
     $rootScope.$on('$viewContentLoaded', ()=> {
       $timeout(() => {
         componentHandler.upgradeAllRegistered();
       })
     })
   }).config(function(cfpLoadingBarProvider) {
     // true is the default, but I left this here as an example:
     cfpLoadingBarProvider.includeSpinner = false;
   });