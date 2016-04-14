angular
    .module
        ('architectplay',
            ['ngRoute',
             'ngResource',
             'ngAria',
             'ngAnimate',
             'angular-loading-bar',
             'toastr',
             'ngDialog'
            ]
        )
    .config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: '/assets/app/views/home.html',
                controller: 'home.controller',
                activetab: 'home'
            })
            .when('/contato', {
                templateUrl: '/assets/app/views/contato.html',
                controller: 'contato.create.controller',
                activetab: 'contato'
            })
            .when('/contato/detalhe/:id', {
                templateUrl: '/assets/app/views/contatos/detail.html',
                controller: 'contato.detail.controller',
                activetab: 'contato'
            })
            .when('/contatos', {
                templateUrl: '/assets/app/views/contatos/list.html',
                controller: 'contato.list.controller',
                activetab: 'contato'
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
            .when('/usuario/perfil', {
                templateUrl: '/assets/app/views/usuarios/perfil.html',
                controller: 'usuario.perfil.controller',
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
            })
            .otherwise({redirectTo:'/'});
   // se tirar esse .run as funções do material design lite
   //não carrega corretamente na página, precisa apertar f5 várias vezes
   }).run(function ($rootScope,$timeout) {
     $rootScope.$on('$viewContentLoaded', ()=> {
       $timeout(() => {
         componentHandler.upgradeAllRegistered();
       })
     })
   }).config(function(cfpLoadingBarProvider) {
      // carrega o loading bar
     // true is the default, but I left this here as an example:
     cfpLoadingBarProvider.includeSpinner = false;
   }).config(function(toastrConfig) {
        //configurações do toastr
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
   }).directive('myEnter', function () {
        return function (scope, element, attrs) {
            element.bind("keydown keypress", function (event) {
                if(event.which === 13) {
                    scope.$apply(function (){
                        scope.$eval(attrs.myEnter);
                    });
                    event.preventDefault();
                }
            });
        };
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
      }).config(['ngDialogProvider', function (ngDialogProvider) {
            ngDialogProvider.setDefaults({
                showClose: false,
                closeByDocument: false,
                closeByEscape: false,
                className: 'ngdialog-theme-default'
            });
        }]).directive('clickAndDisable', function() {
             return {
               scope: {
                 clickAndDisable: '&'
               },
               link: function(scope, iElement, iAttrs) {
                 iElement.bind('click', function() {
                   iElement.prop('disabled',true);
                   scope.clickAndDisable().finally(function() {
                     iElement.prop('disabled',false);
                   })
                 });
               }
             };
        });