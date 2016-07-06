angular.module('architectplay')
  .factory('BaseUrl', function($location) {
     //return 'http://' + $location.host() + ':9000';
      return '://' + $location.host() + ':9000';
  });