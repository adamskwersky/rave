/*
 * createAccountController
 * Passes our form off to the createAccountApi when the form is submitted
 *
 */

define(function(require) {
  return ['$scope', 'createAccountApi',
    function($scope, createAccountApi) {

      // We call this method when they submit the form.
      $scope.submit = function(accountInfo) {

        createAccountApi.createAccount(accountInfo)

          .then(function(res) {
            $scope.invalid = false;
            $scope.success = true;
            $scope.username = accountInfo.username;
            $scope.email = accountInfo.email;
          })

          .catch(function(res) {
            $scope.invalid = true;
            $scope.error = res.data.message;
          });
      };
    }
  ];
});