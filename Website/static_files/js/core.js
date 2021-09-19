async function getData(resourceName) {
    const raw = await fetch('/data/' + resourceName);
    return await raw.json();
}