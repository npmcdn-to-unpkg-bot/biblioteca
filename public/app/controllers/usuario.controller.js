angular.module('architectplay')
    .controller('usuario.perfil.controller', function ($scope, $rootScope, $routeParams, $location, Usuario, toastr, ngDialog) {

    $scope.mostrar = false;

    $scope.init = function() {
      Usuario.getAutenticado(function(data) {
          $rootScope.usuario = data;
          $scope.mostrar = true;
       },function(data) {
            $scope.mostrar = false;
            $location.path('/');
            toastr.error('Não autorizado');
         });
       };

       $scope.opendialog = function() {
           ngDialog.open({
               template: 'templateDialog',
               controller:'usuario.perfil.controller'
           });
       };

       $scope.sim = function() {
          Usuario.reset(function(data) {
            toastr.success('O email foi enviado com sucesso!');
            $scope.closeThisDialog('Fechar');
            },function(data) {
                toastr.error(data.data, 'Não foi possível executar esta operação!');
                $scope.closeThisDialog('Fechar');
            });
          };
  });