define(function(require) {
  require('../admin');
  var angular = require('angular');

  angular.module('admin').config([
    '$stateProvider', '$urlRouterProvider',
    function($stateProvider, $urlRouterProvider) {
      $stateProvider
        .state('portal.admin.preferences', {
          url: '/preferences',
          templateUrl: '/subapps/admin/preferences/preferences.html',
          authenticate: true
        });
    }
  ]);
});
