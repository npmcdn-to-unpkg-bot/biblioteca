angular
    .module
        ('architectplay',
            ['ui.router',
             'ngResource',
             'ngSanitize',
             'ngAria',
             'ngAnimate',
             'angular-loading-bar',
             'toastr',
             'ngDialog', 
             'youtube-embed', 
             'angular-carousel',
             'ngTwitter',
             'ngCordova',
             'angular-oauth2'
            ]
        )
    .config(function ($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise("/");

        $stateProvider
            .state('home', {
                url: "/",
                templateUrl: 'assets/app/views/home.html',
                controller: 'home.controller'
            })
            .state('usuarios', {
                url: "/usuario/perfil",
                templateUrl: 'assets/app/views/usuarios/perfil.html',
                controller: 'usuario.perfil.controller'
            })
            .state('livros', {
                url: "/livros",
                templateUrl: 'assets/app/views/livros/list.html',
                controller: 'livro.list.controller'
            })
            .state('cursos', {
                url: "/cursos",
                templateUrl: 'assets/app/views/cursos/list.html',
                controller: 'curso.list.controller'
            })
            .state('artigos', {
                url: "/artigos",
                templateUrl: 'assets/app/views/artigos/list.html',
                controller: 'artigo.list.controller'
            })
            .state('videos', {
                url: "/videos",
                templateUrl: 'assets/app/views/videos/list.html',
                controller: 'video.list.controller'
            })
            .state('fotos', {
                url: "/fotos",
                templateUrl: 'assets/app/views/fotos/list.html',
                controller: 'foto.controller',
                activetab: 'fotos'
            })
            .state('fotos/biometano', {
                url: "/fotos/biometano",
                templateUrl: 'assets/app/views/fotos/biometano/list.html',
                controller: 'foto.controller',
                activetab: 'fotos'
            })
            .state('fotos/granjahaacke', {
                url: "/fotos/granjahaacke",
                templateUrl: 'assets/app/views/fotos/granjahaacke/list.html',
                controller: 'foto.controller',
                activetab: 'fotos'
            })
            .state('fotos/laboratoriobiogas', {
                url: "/fotos/laboratoriobiogas",
                templateUrl: 'assets/app/views/fotos/laboratoriobiogas/list.html',
                controller: 'foto.controller',
                activetab: 'fotos'
            })
            .state('fotos/unidadesdeproducao', {
                url: "/fotos/unidadesdeproducao",
                templateUrl: 'assets/app/views/fotos/unidadesdeproducao/list.html',
                controller: 'foto.controller',
                activetab: 'fotos'
            })
            .state('direitos', {
                url: "/direitos",
                templateUrl: 'assets/app/views/direito.html',
                controller: 'direito.controller'
            })
            .state('biogas/sobre', {
                url: "/biogas/sobre",
                templateUrl: 'assets/app/views/biogas/sobre.html',
                controller: 'sobre.controller'
            })
            .state('casos', {
                url: "/casos",
                templateUrl: 'assets/app/views/casos/list.html',
                controller: 'casos.controller'
            })
            .state('ferramentas', {
                url: "/ferramentas",
                templateUrl: 'assets/app/views/ferramentas/list.html',
                controller: 'ferramentas.controller'
            })
            .state('ped', {
                url: "/ped",
                templateUrl: 'assets/app/views/ped/list.html',
                controller: 'ped.controller'
            })
            .state('marcos', {
                url: "/marcos",
                templateUrl: 'assets/app/views/marcos/list.html',
                controller: 'marcos.controller'
            })
            .state('aspectos', {
                url: "/aspectos",
                templateUrl: 'assets/app/views/aspectos/list.html',
                controller: 'aspectos.controller'
            })
            .state('registros', {
                url: "/registros",
                templateUrl: 'assets/app/views/registros/list.html',
                controller: 'registros.controller'
            })
            .state('eventos', {
                url: "/eventos",
                templateUrl: 'assets/app/views/eventos/list.html',
                controller: 'evento.list.controller'
            })
            .state('noticias', {
                url: "/noticias",
                templateUrl: 'assets/app/views/noticias/list.html',
                controller: 'noticia.list.controller'
            })
            .state('publicacoes', {
                url: "/publicacoes",
                templateUrl: 'assets/app/views/publicacoes/list.html',
                controller: 'publicacao.list.controller'
            });
    }).config(function($httpProvider, cfpLoadingBarProvider) {
        cfpLoadingBarProvider.includeSpinner = false;
    }).config(function(toastrConfig) {
        angular.extend(toastrConfig, {
            positionClass: 'toast-bottom-right',
            allowHtml: false,
            closeButton: true,
            closeHtml: '<button>&times;</button>',
            extendedTimeOut: 1000,
            iconClasses: {
                error: 'toast-error',
                info: 'toast-info',
                success: 'toast-success',
                warning: 'toast-warning'
            },
            messageClass: 'toast-message',
            onHidden: null,
            onShown: null,
            onTap: null,
            progressBar: false,
            tapToDismiss: true,
            templates: {
                toast: 'directives/toast/toast.html',
                progressbar: 'directives/progressbar/progressbar.html'
            },
            timeOut: 4000,
            titleClass: 'toast-title',
            toastClass: 'toast'
       });
    }).run(function ($rootScope) {
        $rootScope.$on('$viewContentLoaded', function upgradeAllRegistered() {
            componentHandler.upgradeAllRegistered();
        });
    }).run(function ($rootScope) {
        $rootScope.Messages = window.Messages;
    }).run(function($rootScope, $state) {
        $rootScope.$state = $state;
    }).directive('clickOnce', function($timeout) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var replacementText = attrs.clickOnce;
                element.bind('click', function() {
                    $timeout(function() {
                        if (replacementText) {
                            element.html(replacementText);
                        }
                        element.attr('disabled', true);
                    }, 0);
                });
            }
        };
    }).config(['OAuthProvider', function(OAuthProvider) {
        OAuthProvider.configure({
            baseUrl: 'https://api.twitter.com/oauth/autorize',
            clientId: 'Dt5PurvXo385qri86oQ1tMMrS',
            clientSecret: 'YlD9V4GmFr8IlcND03nuWVAsFknEhOqT33jLjyDYVod08tFYql' // optional
    });
}]);