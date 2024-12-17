
function select(tag)
{
		var maindoc=document;
		var all=maindoc.getElementsByName('relation');
		for(i=0;i<all.length;i++)
		{
			var e=all[i];
			e.style.visibility='collapse';
		}
		var e=maindoc.getElementById(tag);
		e.style.visibility='visible';
}
function menu()
{
		var maindoc=document;
		var toc=maindoc.getElementById('toc');
		if((toc.style.display != 'inline'))
			toc.style.display='inline';
		else
			toc.style.display='none';
}
