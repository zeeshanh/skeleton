
// This is the idiomatic way to ensure that JQuery does not
// execute until the page has loaded
$(function(){
    const api = ""; // <- do not need a root api URL if this page is served directly by the API

    $.getJSON(api+"/receipts", function(receipts){
        for(var i=0; i < receipts.length; i++) {
            var receipt = receipts[i];
            $('<div class="receipt">${receipt.merchantName}<div><span class="receiptTag">t1</span></div></div>')
                .appendTo($("#receiptList"));
        }

    })
})
