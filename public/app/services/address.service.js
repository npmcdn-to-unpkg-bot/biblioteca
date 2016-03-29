angular.module('architectplay')
    .service('Usuario',['$resource', 'BaseUrl',
      function($resource, BaseUrl){
        return $resource(BaseUrl + '/usuario/:id', {}, {
           cadastrar: {method: 'POST', url: BaseUrl + '/usuario/cadastrar', isArray: false},
           update: {method: 'PUT', url: BaseUrl + '/usuario/:id', isArray: false},
           getAll: {method: 'GET', url: BaseUrl + '/usuarios', isArray: true},
           getFiltroUsuarios: {method: 'GET', url: BaseUrl + '/usuarios/filtro/:filtro', isArray: true},
           getAutenticado: {method: 'GET', url: BaseUrl + '/current', isArray: false}
        });
    }]);