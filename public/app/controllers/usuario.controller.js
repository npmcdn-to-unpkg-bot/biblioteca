angular.module('architectplay')
    .controller('usuario.perfil.controller', function ($scope, $rootScope, $routeParams, $location, Usuario, toastr, ngDialog) {

    $rootScope.title = Messages('menu.top.title.17');

    $scope.mostrar = false;

    $scope.init = function() {
      Usuario.getAutenticado(function(data) {
          $rootScope.usuario = data;
          $scope.mostrar = true;
       },function(data) {
            $scope.mostrar = false;
            $location.path('/');
            toastr.error(Messages('client.error'));
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
            toastr.success(Messages('client.send.email.message'));
            $scope.closeThisDialog('Fechar');
            },function(data) {
                toastr.error(data.data, Messages('client.error.operation'));
                $scope.closeThisDialog('Fechar');
            });
          };
  });