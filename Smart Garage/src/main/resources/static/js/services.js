const $list = $('#service-list')
$(document).on('click', '#show-all-btn', () => {
    $list.empty();

    const url = new URL('http://localhost:8080/api/services')
    export const $allServices = fetch(url)
        .then((res) => res.json())
        .then((json) => {
            console.log(json);

            json.data.forEach((service) => {
                $list.append(<a>${service.serviceName}</a>)
            });
        });
});