
/* Canvas for Rock Vision 3D */

function RV3DRawCanvas(canvas)
{
	var self = this;
	self._canvas = canvas;
	
	self.prepare = function(size) {
			//self._canvas.width = size.width;
			//self._canvas.height = size.height;
		};
		
	self.getImageData = function() {
			var width = self._canvas.width;
			var height = self._canvas.height;
			
			return self._canvas.getContext("2d").getImageData(0, 0, width, height);
		};
	
	return self;
}

function RV3DImageCanvas(image, bounds, canvasSize)
{
	var self = this;
	self._image = image;
	self._bounds = bounds;
	self._canvas = document.createElement("canvas");
	var nodes = document.getElementsByTagName("body");
	if(nodes.length == 0) {
		throw new Error("Invalid DOM");
	}
	self._canvas.width = canvasSize.width;
	self._canvas.height = canvasSize.height;
	nodes.item(0).appendChild(self._canvas);
	
	self.prepare = function(size) {
			var cw = self._canvas.width;
			var ch = self._canvas.height;
			var ctx = self._canvas.getContext("2d");
			ctx.fillStyle = "black";
			ctx.fillRect(0, 0, cw, ch);
		
			var sx = self._bounds.x;
			var sy = self._bounds.y;
			var sw = self._bounds.width;
			var sh = self._bounds.height;
			
			var scaleX = cw / sw;
			var scaleY = ch / sh;
			var scale = Math.min(scaleX, scaleY);
			
			var dw = parseInt(sw * scale);
			var dh = parseInt(sh * scale);
			var dx = parseInt((cw - dw) / 2);
			var dy = parseInt((ch - dh) / 2);
			ctx.drawImage(self._image, sx, sy, sw, sh, dx, dy, dw, dh);
		};
		
	self.getImageData = function() {
			var width = self._canvas.width;
			var height = self._canvas.height;
			
			return self._canvas.getContext("2d").getImageData(0, 0, width, height);
		};
	
	return self;
}

function RV3DCanvas(canvas)
{
	var self = this;
	self.DEFAULT_WIDTH = 800;
	self.DEFAULT_HEIGHT = 600;
	self._canvas = canvas;
	self._right = null;
	self._left = null;
	self.flipVertical = false;
	
	self.dispose = function() {
			document.removeEventListener("fullscreenchange", self_fullscreenChanged);
			document.removeEventListener("mozfullscreenchange", self_fullscreenChanged);
			document.removeEventListener("webkitfullscreenchange", self_fullscreenChanged);
		};
	
	self.setRight = function(canvas, bounds) {
			var obj = null;
			if(canvas.tagName == null) {
				obj = canvas;
			}else if(canvas.tagName.toLowerCase() == "canvas") {
				obj = new RV3DRawCanvas(canvas);
			}else{
				obj = new RV3DImageCanvas(canvas, bounds, {width: self.DEFAULT_WIDTH, height: self.DEFAULT_HEIGHT});
			}
			self._right = obj;
		};
		
	self.setLeft = function(canvas, bounds) {
			var obj = null;
			if(canvas.tagName == null) {
				obj = canvas;
			}else if(canvas.tagName.toLowerCase() == "canvas") {
				obj = new RV3DRawCanvas(canvas);
			}else{
				obj = new RV3DImageCanvas(canvas, bounds, {width: self.DEFAULT_WIDTH, height: self.DEFAULT_HEIGHT});
			}
			self._left = obj;
		};
		
	self.draw = function(size) {
			var ctx = self._canvas.getContext("2d");
			var w = parseInt(self._canvas.width);
			var h = parseInt(self._canvas.height);
			if(size != null) {
				w = size.width;
				h = size.height;
			}
			var coffs = 4;
			
			var leftData = null;
			if(self._left != null) {
				leftData = self._left.getImageData();
			}
			var rightData = null;
			if(self._right != null) {
				rightData = self._right.getImageData();
			}
			if(rightData == null) {
				rightData = leftData;
			}
			if(leftData == null) {
				leftData = rightData;
			}
			
			var buf = ctx.createImageData(w, h);
			var bufData = buf.data;
			for(var y = 0; y < h; y ++) {
				for(var x = 0; x < w; x ++) {
					var sourceOffset = x * coffs + y * (w * coffs);
					var dy = y;
					if(self.flipVertical) {
						dy = h - y - 1;
					}
					var destOffset = x * coffs + dy * (w * coffs);
					var targetData = null;
					if(x % 2 != 0) {
						targetData = rightData;
					}else{
						targetData = leftData;
					}
					var r = 0;
					var g = 0;
					var b = 0;
					if(targetData != null) {
						r = targetData.data[sourceOffset + 0];
						g = targetData.data[sourceOffset + 1];
						b = targetData.data[sourceOffset + 2];
					}
					bufData[destOffset + 0] = r;
					bufData[destOffset + 1] = g;
					bufData[destOffset + 2] = b;
					bufData[destOffset + 3] = 0xff;
				}
			}
			ctx.putImageData(buf, 0, 0);
		};
		
	self.start = function() {
			self.draw();
			console.log("Canvas: requestFullscreen=" + self._canvas.requestFullscreen + ", webkitRequestFullScreen=" + self._canvas.webkitRequestFullScreen + ", mozRequestFullScreen=" + self._canvas.mozRequestFullScreen);
			if(self._canvas.requestFullscreen != null) {
				self._canvas.requestFullscreen();
			}else if(self._canvas.webkitRequestFullScreen != null) {
				self._canvas.webkitRequestFullScreen();
			}else if(self._canvas.mozRequestFullScreen != null) {
				self._canvas.mozRequestFullScreen();
			}
		};
	
	self._fullscreenChanged = function(event) {
			var size = {width: self._canvas.offsetWidth, height: self._canvas.offsetHeight};
			console.log("Canvas: (" + size.width + ", " + size.height + ")");
			self._canvas.width = size.width;
			self._canvas.height = size.height;
			
			if(self._right != null) {
				self._right.prepare(size);
			}
			if(self._left != null) {
				self._left.prepare(size);
			}
			self.draw(size);
		};

	document.addEventListener("fullscreenchange", self._fullscreenChanged, false);
	document.addEventListener("mozfullscreenchange", self._fullscreenChanged, false);
	document.addEventListener("webkitfullscreenchange", self._fullscreenChanged, false);
	
	return self;
}

