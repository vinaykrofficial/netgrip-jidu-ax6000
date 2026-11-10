'use strict';
'require view';
'require ui';

/*
 * netgrip/panel: embeds the netgrip companion UI (served by the netgrip
 * package on port 8090) inside LuCI.
 */

return view.extend({
	render: function() {
		var isHttps = window.location.protocol === 'https:';
		var url = window.location.protocol + '//' + window.location.hostname + ':8090/';

		return E('div', { 'class': 'netgrip-embed' }, [
			E('div', { 'style': 'margin-bottom:12px;display:flex;align-items:center;gap:12px;flex-wrap:wrap;' }, [
				E('span', { 'class': 'cbi-section-descr', 'style': 'margin:0;font-weight:500;' },
					_('NetGrip Companion Panel for Jidu AX6000 is active on port 8090.')
				),
				E('a', { href: url, target: '_blank', rel: 'noreferrer', 'class': 'cbi-button cbi-button-action' },
					_('Open NetGrip Panel In New Tab')
				),
				isHttps ? E('span', { 'style': 'color:#f59e0b;font-size:12px;' },
					_('Note: If LuCI is on HTTPS, browser security may block HTTP iframes. Click Open in New Tab or access via HTTP.')
				) : ''
			]),
			E('iframe', {
				src: url,
				id: 'netgrip-frame',
				style: 'width:100%;min-height:84vh;border:1px solid #1f2937;border-radius:8px;background:#0b1118;'
			})
		]);
	}
});
