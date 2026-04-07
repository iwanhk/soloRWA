import PAGES_JSON from '@/pages.json';

interface Page {
	path :string,
	aliasPath? :string,
	name? :string,
}
type Route = Record<string, string>;
interface TabBar {
	pagePath :string,
	text: string,
}

function getPagesRoutes(pages: Page[], rootPath:string|null|undefined = null) {
	const routes:Route[] = [];
	for (let i = 0; i < pages.length; i++) {
		const item = pages[i];
		const route:Route = {};
		for(let key of ['path', 'aliasPath', 'name'] as (keyof Page)[]){
			let value = item?.[key];
			if (key === 'path') {
				value = rootPath ? `/${rootPath}/${value}` : `/${value}`
			}
			if (key === 'aliasPath' && i == 0 && rootPath == null) {
				route[key] = route[key] || '/'
			} else if (value !== undefined) {
				route[key] = value;
			}
		}
		routes.push(route);
	}
	return routes;
}

export const pages = getPagesRoutes(PAGES_JSON.pages);
//@ts-ignore
for(let subPackage of PAGES_JSON.subPackages??[]){
	pages.push(...getPagesRoutes(subPackage.pages, subPackage.root));
}
export const tabs = PAGES_JSON.tabBar?.list as TabBar[]|undefined;