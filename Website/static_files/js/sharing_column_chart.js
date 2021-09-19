async function renderSharingColumnChart(element) {
  const data = await getData('sharing_column_chart.json')
  
  const options = {
    title: {
      text: 'Items Shared Among Us',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      title: {
        text: 'Count',
      },
    },
  }
  
  new ApexCharts(element, options).render();
}
