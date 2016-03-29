angular.module('architectplay')
  .controller('drawer', function ($scope, $rootScope, $route, Usuario) {

  //para funcionar o selected do menu através do ngRoute
    $scope.$route = $route;

});