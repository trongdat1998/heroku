var weatherApp = angular.module('weatherApp', []);
weatherApp.controller('WeatherCtrl', function ($scope, $http) {
    $scope.form = {};
    $scope.items = [];
    $scope.create = function () {
        var item = angular.copy($scope.form);
        $http.post('/vnpay').then(resp => {
            window.location.assign(""+resp.data.data)
        }).catch(error => {
            console.log("Error", error);
        });
    }
});