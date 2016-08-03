angular.module('architectplay')
  .factory('BaseUrl', function($location) {
     //return 'http://' + $location.host() + ':9000';
      //return 'https://' + $location.host() + ':9000';
      //return $location.protocol() + '://' + $location.host() + ':9000';
      //return $location.protocol() + '://' + $location.host() + ':' + $location.port() + '/architectplay-1.0-SNAPSHOT';
      return $location.protocol() + '://' + $location.host() + ':' + $location.port();
  });