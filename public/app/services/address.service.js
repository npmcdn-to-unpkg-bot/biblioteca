angular.module('architectplay')
    .service('Usuario',['$resource', 'BaseUrl',
      function($resource, BaseUrl){
        return $resource(BaseUrl + '/usuarios/:id', {}, {
           cadastrar: {method: 'POST', url: BaseUrl + '/usuarios/cadastrar', isArray: false},
           getAll: {method: 'GET', url: BaseUrl + '/usuarios', isArray: true},
           update: {method: 'PUT', url: BaseUrl + '/usuarios/:id', isArray: false},
           getFiltroUsuario: {method: 'GET', url: BaseUrl + '/usuarios/filtro/:filtro', isArray: true}
        });
    }]);