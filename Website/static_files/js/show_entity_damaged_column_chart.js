async function renderEntityDamagedColumnChart(element) {
  const data = await getData('entity_damaged_column_chart.json')
  
  const options = {
    title: {
      text: 'Damage From Entities By Player',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    xaxis: {
      categories: data.categories,
    },
    dataLabels: {
      formatter: function (val) {
        return val.toFixed(0);
      },
    },
    yaxis: {
      title: {
        text: 'Damage',
      },
      labels: {
        formatter: function (val) {
          return val.toFixed(0);
        },
      },
    },
  }
  
  new ApexCharts(element, options).render();
}
