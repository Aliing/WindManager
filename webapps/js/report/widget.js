AeroHive =
{
  text: function (div,message)
  {
    div.appendChild
    (
      document.createTextNode( message)
    );
  },
  textId: function (div,message)
  {
    text
    (
      document.getElementById( id),
      message
    );
  }
};