angular.module('architectplay')
  .factory('BaseUrl', function($location) {
     return 'https://' + $location.host() + ':9000' ;
   });