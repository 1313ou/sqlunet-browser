/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.loaders

import org.sqlunet.bnc.provider.BNCContract.Words_BNCs
import org.sqlunet.browser.Module.ContentProviderSql

object Queries {
    
    @JvmStatic
    fun prepareBnc(wordId: Long, pos: Character?): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = Words_BNCs.URI
        providerSql.projection = arrayOf(
            Words_BNCs.POSID, Words_BNCs.FREQ, Words_BNCs.RANGE, Words_BNCs.DISP,  
            Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.FREQ1 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.FREQ1,  
            Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.RANGE1 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.RANGE1,  
            Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.DISP1 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.DISP1,  
            Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.FREQ2 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.FREQ2,  
            Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.RANGE2 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.RANGE2,  
            Words_BNCs.BNCCONVTASKS + '.' + Words_BNCs.DISP2 + " AS " + Words_BNCs.BNCCONVTASKS + Words_BNCs.DISP2,  
            Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.FREQ1 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.FREQ1,  
            Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.RANGE1 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.RANGE1,  
            Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.DISP1 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.DISP1,  
            Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.FREQ2 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.FREQ2,  
            Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.RANGE2 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.RANGE2,  
            Words_BNCs.BNCIMAGINFS + '.' + Words_BNCs.DISP2 + " AS " + Words_BNCs.BNCIMAGINFS + Words_BNCs.DISP2,  
            Words_BNCs.BNCSPWRS + '.' + Words_BNCs.FREQ1 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.FREQ1,  
            Words_BNCs.BNCSPWRS + '.' + Words_BNCs.RANGE1 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.RANGE1,  
            Words_BNCs.BNCSPWRS + '.' + Words_BNCs.DISP1 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.DISP1,  
            Words_BNCs.BNCSPWRS + '.' + Words_BNCs.FREQ2 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.FREQ2,  
            Words_BNCs.BNCSPWRS + '.' + Words_BNCs.RANGE2 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.RANGE2,  
            Words_BNCs.BNCSPWRS + '.' + Words_BNCs.DISP2 + " AS " + Words_BNCs.BNCSPWRS + Words_BNCs.DISP2
        )
        providerSql.selection = if (pos == null) Words_BNCs.WORDID + " = ?" else Words_BNCs.WORDID + " = ? AND " + Words_BNCs.POSID + "= ?"
        providerSql.selectionArgs = if (pos == null) arrayOf(wordId.toString()) else arrayOf(wordId.toString(), pos.charValue().toString())
        return providerSql
    }
}
