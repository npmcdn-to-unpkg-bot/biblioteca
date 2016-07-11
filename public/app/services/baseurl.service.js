angular.module('architectplay')
  .factory('BaseUrl', function($location) {
     return $location.host();
      //return $location.protocol() + '://' + $location.host() + ':9000';
  });