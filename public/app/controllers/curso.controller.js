angular.module('architectplay')
  .controller('curso.list.controller', function ($scope, Curso, toastr) {
      $scope.init = function() {
          Curso.getAll(function(data) {
              $scope.cursos = data;
          }, function() {
              toastr.error('Não autorizado.');
          });
      };
});