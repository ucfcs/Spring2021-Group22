async function renderEntityDamageInteractionColumnChart(element) {
  const data = await getData('entity_damage_interactions_column_chart.json')
  
  const options = {
    title: {
      text: 'Entity Damage',
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
        text: 'Total',
      },
    },
  }
  
  new ApexCharts(element, options).render();
}
