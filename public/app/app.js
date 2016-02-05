angular
    .module
        ('architectplay',
            ['ngRoute',
             'ngResource',
             'ngAria'
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
            })
            .when('/cursos', {
                templateUrl: '/assets/app/views/cursos/list.html',
                controller: 'curso.controller'
            })
            .when('/artigos', {
                templateUrl: '/assets/app/views/artigos/list.html',
                controller: 'artigo.controller'
            })
            .when('/artigos/energiasrenovaveis/biogas', {
                templateUrl: '/assets/app/views/artigos/energiasrenovaveis/biogas.html',
                controller: 'artigo.controller'
            });
   // se tirar esse .run as funções do material design lite
   //não carrega corretamente na página, precisa apertar f5 várias vezes
   }).run(function ($rootScope,$timeout) {
     $rootScope.$on('$viewContentLoaded', ()=> {
       $timeout(() => {
         componentHandler.upgradeAllRegistered();
       })
     })
   });