package com.cxkj.common.widget.imagerv




class ImageModelBean constructor(
    var modelType : Int = ImageRecyclerView.ADD_ITEM , var localPath : String? ,
    var netPath : String? ,
) {
    constructor(type : Int) : this(type , null , null)
}
