angular.module('architectplay')
    .controller('artigo.controller', function ($rootScope, $log) {
        console.log('Controller Artigo');
        $rootScope.title = 'Artigos';
    });