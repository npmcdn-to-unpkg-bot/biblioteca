angular.module('architectplay')
  .controller('curso.list.controller', function ($scope, $rootScope, Curso, toastr, $routeParams, $location) {
       // $rootScope.title = Messages('menu.top.title.2');
      
      $scope.init = function() {
          Curso.getAll(function(data) {
              $scope.cursos = data;
          }, function(data) {
              $location.path('/');
              toastr.error('Não autorizado.');
          });
      };
});