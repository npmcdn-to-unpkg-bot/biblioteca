angular.module('architectplay')
  .factory('BaseUrl', function($location) {
     //return 'http://' + $location.host() + ':9000';
      //return 'http://' + $location.host() + ':9000';
      //return $location.protocol() + '://' + $location.host() + ':9000';
      return $location.protocol() + '://' + $location.host() + ':' + $location.port();
  });