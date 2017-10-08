(function () {
  $("#search-badges-button").click(function() {
    $("#search-badges").empty();
    var receiverLogin = $("#github-search-login").val();
    $.get(`/badges?receiverLogin=${receiverLogin}`, function (data) {
        console.log(data);
        data.forEach(function(badge) {
          var badge =
            `<div class="one-fifth column badge">
              <img class="avatar" width="72" height="72" src="${badge.from.avatar_url}">
              <h3>${badge.name}</h3>
            </div>`;
          $("#search-badges").append(badge);
        });
      });
  });
}());
