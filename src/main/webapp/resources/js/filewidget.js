// Widget that controls the file upload and the display of the filetable.

$(document).ready(function() {

//update on page load 
updateFileTable(); 

var fileupload = $('#fileupload').fileupload({
	replaceFileInput: false,
	maxNumberOfFiles: 1,
	add: function (e, data) {
        $('#selectedFile').val(data.files[0].name + " (" + bytesToSize(data.files[0].size) + ")");
		data.context = $('#startUpload') 
            .click(function () {
                fileupload = data.submit();
            });
    },
    done: function(e, data) {
    	updateFileTable();
    },
    progressall: function(e, data) { 
		var progress = parseInt(data.loaded / data.total * 100, 10);
		$('#progress').css(
		'width',
		progress + '%'
		);
		$('#progress').html(progress + "%");
    }
});

$('#cancelUpload').click(function(e) {
	fileupload.abort();
});

// Retrieves the available files from the server and displays them as a table
function updateFileTable() {
	$.get('/Twintiment/files')
		.success(function(msg) {
			
			$('#fileTable tbody').children().remove() //clear table
			
			msg.forEach(function(file) {
				//Add row to table
				$('#fileTable').find('tbody')
					.append($('<tr>')
							.append($('<td>')
								.append($('<input type="radio" name="file" value=' + file.fileName + '><br>'))
							.append($('</td>')))
							.append($('<td>')
								.append(file.fileName)
							.append($('</td>')))
							.append($('<td>')
									.append(bytesToSize(file.fileSize))
							.append($('</td>')))
					.append($('</tr>')));	
			}); //foreach

			//Click listeners for radio buttons
			$('#fileTable tbody input:radio').click(function() { 
				streamer.selectedFile = $(this).prop('value')
			});
		}) //success
		.error(function(msg) {
			console.log(msg);
		});
}

// Converts a filesize in bytes to highest applicable potency
function bytesToSize(bytes) {
	   var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
	   if (bytes == 0) return '0 Byte';
	   var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
	   return Number(bytes / Math.pow(1024, i), 2).toFixed(2) + ' ' + sizes[i];
	};

});